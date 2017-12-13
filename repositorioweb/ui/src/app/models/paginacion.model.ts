export class Paginacion {
  public pagina: number;
  public porpagina: number;
  public inicio: number;
  public total: number;
  public mostrando: number;
  public paginas: number;
  public orden: string;
  public direccion: string;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
  /*constructor(pagina: number, porpagina: number, inicio: number, total: number, mostrando: number, paginas: number, orden: string, direccion: string) {
    this.pagina = pagina;
    this.porpagina = porpagina;
    this.inicio = inicio;
    this.total = total;
    this.mostrando = mostrando;
    this.paginas = paginas;
    this.orden = orden;
    this.direccion = direccion;
  }*/
}
