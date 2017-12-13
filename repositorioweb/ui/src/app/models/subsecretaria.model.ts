import {Ministerio} from './ministerio.model';

export class Subsecretaria {
  public id: number = 0;
  public ministerio_id: number;
  public nombre: string = '';
  public habilitado: boolean = true;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }

  getMinisterio (ministerios: Ministerio[]): Ministerio {
    if ( ministerios.length > 0 ) {
      for (let item of ministerios) {
        if( Number(item.id) === this.ministerio_id ) {
          return item;
        }
      }
    }
    return new Ministerio({});
  }
}
