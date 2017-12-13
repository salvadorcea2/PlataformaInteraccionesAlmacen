export class Plantilla {
  public id: number;
  public nombre: string;
  public descripcion: string;
  public plantilla: string;
  public habilitado: boolean;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
