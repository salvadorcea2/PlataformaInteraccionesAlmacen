export class Mantenedor {
  public tabla: string = '';
  public singular: string = '';
  public plural: string = '';

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}

export class MantenedorItem {
  public tabla: string = '';
  public id: number = 0;
  public nombre: string = '';
  public descripcion: string = '';
  public habilitado: boolean = true;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
