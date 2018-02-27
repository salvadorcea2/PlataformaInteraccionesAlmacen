export class Tramite {
  public id: number;
  public nombre: string = '';
  public descripcion: string = '';
  public institucion_id: number;
  public codigo_pmg: string = '';
  public url: string = '';
  public periodicidad_id: number;
  public comentarios: string = '';
  public clave_unica: boolean = false;
  public costo: number;
  public codigo_simple: string = '';
  public tiempo_espera: number;
  public categoria_id: number;
  public nivel_digitalizacion_id: number;
  public presencialidad: boolean = false;
  public barreras_normativas: string = '';
  public fuente: string = '';
  public habilitado: boolean = true;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}

export class TramiteFactor {
  public id: number;
  public tipo_tramite_id: number;
  public tipo_interaccion_id: number;
  public factor: number = 1;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}

export class TramiteFactorHistorico {
  public id: number;
  public tipo_tramite_id: number;
  public tipo_interaccion_id: number;
  public factor: number;
  public fecha_creacion: string;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
