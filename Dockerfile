FROM eclipse-temurin:17-jre-centos7

EXPOSE 8192

ARG MC_HELPER_VERSION=1.20.3
ARG MC_HELPER_BASE_URL=https://github.com/itzg/mc-image-helper/releases/download/v${MC_HELPER_VERSION}
RUN curl -fsSL ${MC_HELPER_BASE_URL}/mc-image-helper-${MC_HELPER_VERSION}.tgz \
  | tar -C /usr/share -zxf - \
  && ln -s /usr/share/mc-image-helper-${MC_HELPER_VERSION}/bin/mc-image-helper /usr/bin

VOLUME ["/data"]
WORKDIR /data
ENV UID=1000 GID=1000

COPY --chmod=755 docker-entrypoint.sh /

CMD ["/docker-entrypoint.sh"]
