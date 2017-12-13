import {Pipe, PipeTransform} from '@angular/core';
import { NgForm } from '@angular/forms';

@Pipe({
  name: 'filtro'
})
export class FiltroPipe implements PipeTransform {
  transform(value: NgForm): any {
    let filtro: any = {};

    for (let key of Object.keys(value.controls)) {
      if ( key === 'tripleta' ) {
        if ( value.controls[key].value.ministerio_id !== '' ) {
          filtro.ministerio_id = value.controls[key].value.ministerio_id;
        }
        if ( value.controls[key].value.subsecretaria_id !== '' ) {
          filtro.subsecretaria_id = value.controls[key].value.subsecretaria_id;
        }
        if ( value.controls[key].value.institucion_id !== '' ) {
          filtro.institucion_id = value.controls[key].value.institucion_id;
        }
      }else if ( value.controls[key].value !== '' ) {
        if( key === 'id' ) {
          filtro[key] = Number( value.controls[key].value );
        }else{
          filtro[key] = value.controls[key].value;
        }
      }
    }

    return filtro;
  }
}
