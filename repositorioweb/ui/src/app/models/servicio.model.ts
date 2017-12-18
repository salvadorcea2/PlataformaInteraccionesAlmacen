export class Servicio {
  public id: number;
  public nombre: string;
  public url: string;
  public institucion_id: number;
  public habilitado: boolean;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
