export class Detallerecepcion {
  public codigo: number;
  public nombre: string;
  public ministerio_id: number;
  public subsecretaria_id: number;
  public institucion_id: number;
  public desde: string;
  public hasta: string;
  public interacciones: number;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
  /*constructor(cod: number, nombre: string, desde: string, hasta: string, interacciones: number) {
    this.codigo = cod;
    this.nombre = nombre;
    this.desde = desde;
    this.hasta = hasta;
    this.interacciones = interacciones;
  }*/
}
