export class Receptor {
  public id: number;
  public nombre: string;
  public descripcion: string;
  public canal_transmision_id: number;
  public formato_id: number;
  public plantilla_recepcion_id: number;
  public periodicidad_id: number;
  public notificacion_diaria: boolean;
  public propiedades: string;
  public habilitado: boolean;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}

export class ReceptorMascara {
  public id: number;
  public receptor_id: number;
  public ministerio_id: number;
  public subsecretaria_id: number;
  public institucion_id: number;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
