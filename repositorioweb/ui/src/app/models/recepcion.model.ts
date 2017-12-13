export class Recepcion {
  public id: number;
  public nombres: string;
  public apellidos: string;
  public fecha_creacion: string;
  public estado: string;
  public archivo: string;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}

export class Factorrecepcion {
  public id: number;
  public recepcion_id: number;
  public tipo_tramite_id: number;
  public tipo_interaccion_id: number;
  public factor: number;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
