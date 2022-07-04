#! /bin/env bash
docker run --name postgresql \
	-e POSTGRES_USER=username \
	-e POSTGRES_PASSWORD=password \
	-e POSTGRES_DB=foresight \
	-p 5432:5432 \
	-d postgres:14