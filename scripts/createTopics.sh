docker exec -ti  kafka  bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create  --replication-factor 1 --partitions 2 --topic items"
docker exec -ti  kafka  bash -c "/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create  --replication-factor 1 --partitions 1 --topic inventory"
