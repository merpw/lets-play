FROM node:16.15.0-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

RUN npx ngcc --properties es2023 browser module main --first-only --create-ivy-entry-points

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist/buy-01-frontend/ /usr/share/nginx/html

EXPOSE 80
