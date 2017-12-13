import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fromlista'
})
export class FromlistaPipe implements PipeTransform {

  transform(value: any, entidad: string, entidadID: number, args?: any): any {
    let lista: any[] = [];
    if ( value.length > 0 ) {
      for (let item of value) {
        if( String(entidadID) !== '' && Number(item[entidad]) === Number(entidadID) ) {
          lista.push(item);
        }
      }
    }
    return lista;
  }

}
