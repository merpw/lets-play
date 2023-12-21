FROM nginx:1.25.3

COPY *.conf /etc/nginx/

COPY init.sh /app/init.sh

CMD /app/init.sh && nginx -g "daemon off;"
