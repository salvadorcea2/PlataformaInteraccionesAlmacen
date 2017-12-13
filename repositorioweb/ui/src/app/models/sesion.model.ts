export class Sesion {
  public id: number;
  public usuario: string;
  public nombres: string;
  public apellidos: string;
  public email: string;
  public telefono: string;
  public rut: string;
  public rol: string;
  public tipo: string;
  public cargo: string;
  public ministerio_id: number;
  public subsecretaria_id: number;
  public institucion_id: number;
  public habilitado: boolean;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
  /*constructor(id: number, usuario: string, nombres: string, apellidos: string, email: string, telefono: string, rut: string, rol: string, tipo: string, cargo: string, habilitado: boolean) {
    this.id = id;
    this.usuario = usuario;
    this.nombres = nombres;
    this.apellidos = apellidos;
    this.email = email;
    this.telefono = telefono;
    this.rut = rut;
    this.rol = rol;
    this.tipo = tipo;
    this.cargo = cargo;
    this.habilitado = habilitado;
  }

  funcion (arg: any){
    arg.entries()
  }*/
}
