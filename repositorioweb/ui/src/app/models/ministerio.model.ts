export class Ministerio {
  public id: number = 0;
  public nombre: string = '';
  public id_cha: string = '';
  public habilitado: boolean = true;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
