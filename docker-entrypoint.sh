#!/usr/bin/env bash

set -e

umask 0002
chmod g+w /data/*

if [[ ! $VOTIFIER_VERSION ]]; then 
  >&2 echo "[warn] \$VOTIFIER_VERSION not set, using latest"
  VOTIFIER_VERSION=latest
fi

RELEASES_URL="https://github.com/xMamo/VanillaVotifier/releases"

RELEASE=$(curl -L -s -H 'Accept: application/json' "${RELEASES_URL}/${VOTIFIER_VERSION}")
RELEASE_TAG=$(echo ${RELEASE} | sed -e 's/.*"tag_name":"\([^"]*\)".*/\1/')

ARTIFACT_URL="${RELEASES_URL}/download/${RELEASE_TAG}/VanillaVotifier.jar"

>&2 echo "[info] Downloading ${ARTIFACT_URL}..."
curl -fsSL "${ARTIFACT_URL}" -o /data/VanillaVotifier.jar

if [[ ! $CONFIG_YAML_URL ]]; then
  >&2 echo "[warn] \$CONFIG_YAML_URL not specified, using default"
  CONFIG_YAML_URL="https://raw.githubusercontent.com/xMamo/VanillaVotifier/${RELEASE_TAG}/src/main/resources/mamo/vanillaVotifier/config.yaml"
fi

>&2 echo "[info] Downloading ${CONFIG_YAML_URL}..."
curl -fsSL "${CONFIG_YAML_URL}" -o /data/config.yaml

if [[ $REPLACE_ENV ]]; then
  >&2 echo "[info] Interpolating config.yaml with CFG variables..."
  mc-image-helper --debug=true interpolate \
    --replace-env-file-suffixes="yaml" \
    --replace-env-prefix="CFG_" \
    "/data"
fi

>&2 echo "[info] Running VanillaVotifier..."
(cd /data && java ${JVM_ARGS} -jar VanillaVotifier.jar)
