export class Usuario {
  public id: number = 0;
  public usuario: string = '';
  public nombres: string = '';
  public apellidos: string = '';
  public email: string = '';
  public telefono: string = '';
  public rut: string = '';
  public rol: string = 'admin';
  public tipo: string = 'representante';
  public cargo: string = '';
  public ministerio_id: number;
  public subsecretaria_id: number;
  public institucion_id: number;
  public habilitado: boolean = true;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }

  getNombre (): string {
    return this.nombres + ' ' + this.apellidos;
  }
}
