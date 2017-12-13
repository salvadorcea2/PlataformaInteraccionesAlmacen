import {Subsecretaria} from './subsecretaria.model';

export class Institucion {
  public id: number = 0;
  public subsecretaria_id: number;
  public nombre: string = '';
  public habilitado: boolean = true;

  constructor(arg: any){
    for (let key of Object.keys(arg)) {
      this[key] = arg[key];
    }
  }

  getSubsecretaria (subsecretarias: Subsecretaria[]): Subsecretaria {
    if ( subsecretarias.length > 0 ) {
      for (let item of subsecretarias) {
        if( Number(item.id) === this.subsecretaria_id ) {
          return item;
        }
      }
    }
    return new Subsecretaria({});
  }
}
