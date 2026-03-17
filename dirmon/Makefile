include .env
export $(shell sed 's/=.*//' .env)

.PHONY: connect-db start-application restart-application stop-application display-logs

connect-db:
	docker compose exec -it database psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}

start-application:
	docker image prune -f
	docker compose down -v
	docker compose up --build -d

restart-application:
	docker compose down
	docker compose build --no-cache
	docker compose up -d

stop-application:
	docker image prune
	docker compose down -v

display-logs:
	docker logs -f application -f