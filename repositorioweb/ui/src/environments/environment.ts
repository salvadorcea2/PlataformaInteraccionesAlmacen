// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  api: {
    host: 'http://localhost:9000/',
    //host: 'https://54.187.41.133:8080/',
    prefix: 'api/',
    // login: 'http://localhost:9000/api/usuario/sesion',
    // login: 'https://54.187.41.133:8080/api/usuario/sesion',
    login: 'http://localhost/segpres/json.php?servicio=login',
    // logout: 'http://localhost/segpres/json.php?servicio=logout'
    logout: 'https://api.claveunica.gob.cl/api/v1/accounts/app/logout'
    // logout: 'https://accounts.claveunica.gob.cl/openid/token'
  }
};
