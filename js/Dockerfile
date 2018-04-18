FROM node:9-alpine

COPY package.json /app/
WORKDIR /app
RUN yarn

COPY src /app/src
COPY webpack.config.js .babelrc /app/
EXPOSE 8080

CMD ["yarn", "start"]