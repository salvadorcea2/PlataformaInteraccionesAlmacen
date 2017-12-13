import { Inject, Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { Paginacion } from '../models/paginacion.model';

@Injectable()
export class PaginationService {
  paginaciones: any[] = [];

  initPagination(name: string, options: any) {
    const cache = this.getPaginacion(name);
    if ( cache != null ) {
      return cache;
    }
    // pagina: number, porpagina: number, inicio: number, total: number, mostrando: number, paginas: number, orden: string, direccion: string)
    const pagina = (typeof options.pagina !== 'undefined' && options.pagina !== null) ? options.pagina : 1;
    const porpagina = (typeof options.porpagina !== 'undefined' && options.porpagina !== null) ? options.porpagina : 10;
    const inicio = (typeof options.inicio !== 'undefined' && options.inicio !== null) ? options.inicio : 0;
    const total = (typeof options.total !== 'undefined' && options.total !== null) ? options.total : 0;
    const mostrando = (typeof options.mostrando !== 'undefined' && options.mostrando !== null) ? options.mostrando : 0;
    const paginas = (typeof options.paginas !== 'undefined' && options.paginas !== null) ? options.paginas : 1;
    const orden = (typeof options.orden !== 'undefined' && options.orden !== null) ? options.orden : 'id';
    const direccion = (typeof options.direccion !== 'undefined' && options.direccion !== null) ? options.direccion : 'desc';
    const p = new Paginacion ({pagina: pagina, porpagina: porpagina, inicio: inicio, total: total, mostrando: mostrando, paginas: paginas, orden: orden, direccion: direccion});
    this.paginaciones.push({key: name, value: p});
    return p;
  }

  getPaginacion(name: string) {
    for ( const data of this.paginaciones ) {
      if ( data.key === name ) {
        return data.value;
      }
    }
    return null;
  }

  update(name: string, pagina: number, porpagina: number, inicio: number, total: number) {
    let paginacion = this.getPaginacion(name);
    paginacion.pagina = pagina;
    paginacion.porpagina = porpagina;
    paginacion.inicio = inicio;
    paginacion.total = total;
    paginacion.mostrando = total < porpagina ? total : porpagina;
    paginacion.paginas = Math.floor(total/10);

    /*this.paginaciones.forEach(function (data, index) {
      if ( data.key === name ) {
        data.value = paginacion;
        this.paginaciones[index] = data;
      }
    });*/

    for (let i = 0; i < this.paginaciones.length; i++) {
      if ( this.paginaciones[i].key === name ) {
        this.paginaciones[i].value = paginacion;
        //this.paginaciones[i] = data;
      }
    }

    return paginacion;
  }
}
