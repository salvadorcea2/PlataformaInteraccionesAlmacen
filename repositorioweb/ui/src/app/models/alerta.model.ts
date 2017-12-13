export class Alerta {
  public type: string;
  public dismissible: boolean;
  public istimeout: boolean;
  public timeout: number;
  public msg: string;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
