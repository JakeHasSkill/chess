#!/usr/bin/env bash

nohup ptyxis -- java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 \
  -cp "client/target/classes:client/build/classes/java/main:out/production/client" \
  client.ClientMain >/dev/null 2>&1 &

disown

# Wait up to 3 seconds for port 5005 to start listening
for i in {1..30}; do
  if ss -tln | grep -q ":5005 "; then
    break
  fi
  sleep 0.1
done

exit 0