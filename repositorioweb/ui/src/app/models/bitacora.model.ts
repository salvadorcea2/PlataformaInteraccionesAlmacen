export class Bitacora {
  public id: number;
  public nivel: string;
  public log: string;
  public etapa: string;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }

  /*constructor(id: number, nivel: string, log: string, etapa: string) {
    this.id = id;
    this.nivel = nivel;
    this.log = log;
    this.etapa = etapa;
  }*/
}
