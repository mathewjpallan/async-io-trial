FROM node:12.16.3 as builder
WORKDIR /app
COPY . /app
RUN npm install

FROM node:12.16.3-slim
COPY --from=builder /app .
EXPOSE 9595
CMD [ "node", "app.js" ]