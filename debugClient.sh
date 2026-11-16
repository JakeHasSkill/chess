#!/usr/bin/env bash

nohup ptyxis -- java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 \
  -jar out/artifacts/client_jar/client.jar >/dev/null 2>&1 &

disown

for i in {1..30}; do
  if ss -tln | grep -q ":5005 "; then
    break
  fi
  sleep 0.1
done

exit 0