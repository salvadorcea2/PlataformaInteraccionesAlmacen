export class Buscador {
  public id: string;
  public entidad: string;
  public filtro: any;
  public paginador: { inicio: number, pagina: number, porpagina: number, total: number };
  public ordenador:  { atributo: string, direccion: string };

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }
}
