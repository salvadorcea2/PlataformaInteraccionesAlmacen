import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'foreignitem'
})
export class ForeignitemPipe implements PipeTransform {

  transform(value: any, lista: any, args?: any): any {
    if ( lista.length > 0 ) {
      for (let item of lista) {
        if (item.id === Number(value)) {
          return item.nombre;
        }
      }
    }
  }

}
