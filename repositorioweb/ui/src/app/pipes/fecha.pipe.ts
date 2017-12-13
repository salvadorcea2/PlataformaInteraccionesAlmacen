import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fecha'
})
export class FechaPipe implements PipeTransform {

  transform(fecha: Date): any {
    let d = fecha.getDate();
    let m = Number(fecha.getMonth()+1);
    return (d < 10 ? '0'+d : d)
      + '/' + (m < 10 ? '0'+m : m)
      + '/' + fecha.getFullYear();
  }

}
