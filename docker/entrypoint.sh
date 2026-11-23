#!/bin/sh
set -eu

WILDFLY_HOME=/opt/jboss/wildfly

if [ -z "${DATABASE_URL:-}" ]; then
    echo "DATABASE_URL is required" >&2
    exit 1
fi

url="${DATABASE_URL#*://}"
credentials="${url%%@*}"
host_and_db="${url#*@}"
db_host_port="${host_and_db%%/*}"
db_name="${host_and_db#*/}"
db_name="${db_name%%\?*}"
db_user="${credentials%%:*}"
db_password="${credentials#*:}"
db_host="${db_host_port%%:*}"
db_port="${db_host_port#*:}"

if [ "$db_port" = "$db_host" ]; then
    db_port=5432
fi

escape_cli() {
    printf '%s' "$1" | sed 's/\\/\\\\/g; s/"/\\"/g'
}

db_user="$(escape_cli "$db_user")"
db_password="$(escape_cli "$db_password")"
db_host="$(escape_cli "$db_host")"
db_name="$(escape_cli "$db_name")"

cat > /tmp/configure-datasource.cli <<EOF
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name="postgresql",driver-module-name="org.postgresql",driver-class-name="org.postgresql.Driver")
/subsystem=datasources/data-source=PostgresDS:add(jndi-name="java:/jdbc/PostgresDS",driver-name="postgresql",connection-url="jdbc:postgresql://${db_host}:${db_port}/${db_name}?sslmode=require",user-name="${db_user}",password="${db_password}",min-pool-size=1,max-pool-size=5,enabled=true)
EOF

"$WILDFLY_HOME/bin/jboss-cli.sh" --file=/tmp/configure-datasource.cli

exec "$WILDFLY_HOME/bin/standalone.sh" \
    -b 0.0.0.0 \
    -bmanagement 127.0.0.1 \
    -c standalone.xml
