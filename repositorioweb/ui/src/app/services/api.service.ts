import { Injectable } from '@angular/core';
import { Http, Headers, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Observer } from 'rxjs/Observer';
import { environment } from '../../environments/environment';

import { DialogoComponent } from '../html/dialogo/dialogo.component';
import { Sesion } from '../models/sesion.model';
import { Buscador } from '../models/buscador.model';
import { Paginacion } from '../models/paginacion.model';

import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

@Injectable()
export class ApiService {

  private _api_host: string;
  private _api_prefix: string;

  private sesion: Sesion;
  private _listas: {entidad: string, params: string, data: any[]}[] = [];
  private _buscadores: Buscador[] = [];
  private _dialogo: {ref: BsModalRef, status: string, nextAction: string};
  private _dialogRef: BsModalRef = null;

  constructor(private http: Http, private modalService: BsModalService) {
    this._api_host = environment.api.host;
    this._api_prefix = environment.api.prefix;

    this._dialogo = {ref: null, status: 'cerrado', nextAction: ''};

    this.modalService.onShow.subscribe(
      (data: any) => {
        this._dialogo.status = 'abierto';
        if( this._dialogo.nextAction !== '') {
          let nextAction = this._dialogo.nextAction;
          this._dialogo.nextAction = '';
          this.dialogo(nextAction);
        }
      }
    );
    this.modalService.onHide.subscribe(
      (data: any) => {
        this._dialogo.status = 'cerrado';
        if( this._dialogo.nextAction !== '') {
          let nextAction = this._dialogo.nextAction;
          this._dialogo.nextAction = '';
          this.dialogo(nextAction);
        }
      }
    );
  }

  /***  DIALOGO  ***/

  dialogo( accion: string ) {
    if ( accion === 'abrir' && this._dialogo.status === 'cerrado' ) {
      const config = {
        animated: false,
        keyboard: false,
        backdrop: true,
        ignoreBackdropClick: true
      };
      this._dialogo.ref = this.modalService.show(DialogoComponent, config);
    }else{
      this._dialogo.nextAction = accion;
    }

    if ( accion === 'cerrar' && this._dialogo.status === 'abierto' && this._dialogo.ref !== null ) {
      this._dialogo.ref.hide();
    }else{
      this._dialogo.nextAction = accion;
    }
  }

  dialogoAbrir() {
    this.dialogo('abrir');
  }
  dialogoCerrar() {
    this.dialogo('cerrar');
  }

  /***  HTTP CRUD  ***/

  create(entidad: string, data: any) {
    const headers = new Headers({'Content-Type': 'application/json'});
    return this.http.post(this._api_host.concat(this._api_prefix, entidad), JSON.stringify(data), {headers: headers} );
  }

  read(entidad: string, params: string) {
    return this.http.get(this._api_host.concat(this._api_prefix, entidad, '?', params));
  }

  update(entidad: string, data: any) {
    const headers = new Headers({'Content-Type': 'application/json'});
    return this.http.put(this._api_host.concat(this._api_prefix, entidad), JSON.stringify(data), {headers: headers} );
  }

  delete(url: string) {
    return this.http.delete(this._api_host.concat(this._api_prefix, url));
  }

  /*** AUTH ***/

  isAuth(): boolean {
    return this.sesion != null;
  }
  isAdmin(): boolean {
    return this.sesion != null && this.sesion.rol === 'admin';
  }
  isUsuario(): boolean {
    return this.sesion != null && this.sesion.rol === 'institucion';
  }
  getSesion(): Sesion{
    return this.sesion;
  }
  setSesion(json: any) {
    const d = json.data.pop();
    this.sesion = new Sesion(d);
  }
  login() {
    return this.http.get(this._api_host + '/api/usuario/sesion' );
    // return this.http.get('http://localhost/segpres/json.php?servicio=login');
  }
  logout() {
    this.sesion = null;
  }

  /***  LISTAS  ***/

  getLista(entidad: string, opciones?: any): Observable<any[]> {
    let params = '';

    let cuantos = 0;
    let orden = 'nombre';
    let direccion = 'asc';
    let habilitado = true;

    if ( typeof opciones !== 'undefined' ) {
      if (typeof opciones.cuantos !== 'undefined' && opciones.cuantos !== null && opciones.cuantos !== 0) {
        cuantos = opciones.cuantos;
      }
      if (typeof opciones.orden !== 'undefined' && opciones.orden !== null && opciones.orden !== '') {
        orden = opciones.orden;
      }
      if (typeof opciones.direccion !== 'undefined' && opciones.direccion !== null && opciones.direccion !== '') {
        direccion = opciones.direccion;
      }
      if (typeof opciones.habilitado !== 'undefined' && opciones.habilitado !== null && opciones.habilitado !== '') {
        habilitado = opciones.habilitado;
      }
    }

    params = 'cuantos=' + cuantos + '&orden=' + orden + '&tipoOrden=' + direccion + '&habilitado=' + habilitado;

    const response = Observable.create((observer: Observer<any[]>) => {
      let cache = true;
      if ( typeof opciones !== 'undefined' && typeof opciones.cache !== 'undefined' && opciones.cache !== null && typeof opciones.cache === 'boolean' ) {
        cache = opciones.cache;
      }
      if ( cache && this._listas.length > 0 ) {
        for (const l of this._listas) {
          if (l.entidad === entidad && l.params === params) {
            observer.next(l.data);
            observer.complete();
            return;
          }
        }
      }

      this.dialogoAbrir();

      this.read(entidad, params)
        .subscribe(
          (response: Response) => {

            this.dialogoCerrar();

            if (response.status !== 200) {
              observer.error(response);
            }

            let data: any;
            try {
              data = response.json();
            } catch (e) {
              // No content response..
              data = null;
            }
            if ( typeof data === 'undefined' || data === null ) {
              observer.error(response);
            }

            let lista = [];
            if( data.data.length > 0 ) {
              for (const d of data.data) {
                lista.push( d );
              }
            }

            this._listas.push( { entidad: entidad, params: params, data: lista } );
            observer.next(lista);
          },
          (error) => { this.dialogoCerrar(); observer.error(error) },
          () => observer.complete()
        );
    });

    return response;
  }

  /*** BUSCADORES ***/

  initBuscador(id: string): Buscador {
    const buscador = new Buscador({
      id: id,
      entidad: id.substring(0, id.indexOf('.')),
      filtro: {},
      paginador: {inicio: 0, pagina: 1, porpagina: 10, total: 0},
      ordenador: {atributo: 'id', direccion: 'desc'}
    });
    this._buscadores.push(buscador);
    return buscador;
  }
  getBuscador(id: string): Buscador {
    if ( this._buscadores.length > 0 ) {
      for (const b of this._buscadores) {
        if (b.id === id ) {
          return b;
        }
      }
    }
    return this.initBuscador(id);
  }
  setBuscador(id: string, buscador: Buscador) {
    if ( this._buscadores.length > 0 ) {
      for (var k = 0; k < this._buscadores.length; k++) {
        if (this._buscadores[k].id === id ) {
          this._buscadores[k] = buscador;
          return;
        }
      }
    }
    this._buscadores.push(buscador);
  }
  loadBuscador(id: string, opciones?: any): Observable<any[]> {
    const buscador: Buscador = this.getBuscador(id);

    let params = '';
    params = params.concat('inicio=', buscador.paginador.inicio.toString());
    params = params.concat('&cuantos=', buscador.paginador.porpagina.toString());
    params = params.concat('&orden=', buscador.ordenador.atributo);
    params = params.concat('&tipoOrden=', buscador.ordenador.direccion);

    let urlParams = new URLSearchParams();
    for (const key of Object.keys(buscador.filtro)) {
      if ( buscador.filtro[key] !== '' ) {
        urlParams.set(key, buscador.filtro[key]);
      }
    }
    if( urlParams.toString() !== '' ) {
      params = params.concat('&', urlParams.toString());
    }

    this.dialogoAbrir();

    const response = Observable.create((observer: Observer<any[]>) => {
      this.read(buscador.entidad, params)
        .subscribe(
          (response: Response) => {
            this.dialogoCerrar();

            if (response.status !== 200) {
              observer.error(response);
            }

            let data: any;
            try {
              data = response.json();
            } catch (e) {
              // No content response..
              data = null;
            }
            if ( typeof data === 'undefined' || data === null ) {
              observer.error(response);
            }

            /*const data = response.json();
            if ( typeof data.data === 'undefined' || data.data === null ) {
              observer.error(response);
            }*/

            buscador.paginador.total = data.total;
            this.setBuscador(id, buscador);
            observer.next(data.data);
          },
          (error) => { this.dialogoCerrar(); observer.error(error); },
          () => { observer.complete(); }
        );
    });
    return response;
  }

  /*** READ ***/

  load (entidad: string, id: number, opciones?: any): Observable<any[]> {
    const buscador = this.getBuscador(entidad + '.load');
    buscador.filtro.id = id;
    return this.loadBuscador(buscador.id);
  }

  /*** CREAR ***/

  crear (entidad: string, data: any, opciones?: any): Observable<any> {
    this.dialogoAbrir();
    const response = Observable.create((observer: Observer<any>) => {
      this.create(entidad, data)
        .subscribe(
          (response: Response) => {

            this.dialogoCerrar();

            if (response.status !== 200) {
              observer.error(response);
            }

            const data = response.json();
            if ( typeof data.data === 'undefined' || data.data === null ) {
              observer.error(response);
            }

            observer.next(data.data);
          },
          (error) => { this.dialogoCerrar(); observer.error(error); },
          () => { observer.complete(); }
        );
    });
    return response;
  }

  /*** ACTUALIZAR ***/

  actualizar (entidad: string, data: any, opciones?: any): Observable<any[]> {
    // if ( data.id === 0 ) {
    //  return this.crear(entidad, data, opciones);
    // }
    this.dialogoAbrir();
    const response = Observable.create((observer: Observer<any>) => {
      this.update(entidad, data)
        .subscribe(
          (response: Response) => {

            this.dialogoCerrar();

            if (response.status !== 200) {
              observer.error(response);
            }

            const data = response.json();
            if ( typeof data.data === 'undefined' || data.data === null ) {
              observer.error(response);
            }

            observer.next(data.data);
          },
          (error) => { this.dialogoCerrar(); observer.error(error); },
          () => { observer.complete(); }
        );
    });
    return response;
  }

  /*** ACTUALIZAR ***/

  borrar (entidad: string, opciones?: any): Observable<any> {
    this.dialogoAbrir();
    const response = Observable.create((observer: Observer<any>) => {
      this.delete(entidad)
        .subscribe(
          (response: Response) => {

            this.dialogoCerrar();

            if (response.status !== 200) {
              observer.error(response);
            }

            let data: any;
            try {
              data = response.json();
            } catch (e) {
              // No content response..
              data = null;
            }
            if ( typeof data === 'undefined' || data === null ) {
              observer.error(response);
            }

            // const data = response.json();
            // if ( typeof data.data === 'undefined' || data.data === null ) {
            //   observer.error(response);
            // }

            observer.next(data);
          },
          (error) => { this.dialogoCerrar(); observer.error(error); },
          () => { observer.complete(); }
        );
    });
    return response;
  }



  /*** Usuarios ***/

  getUsuarios(paginacion: Paginacion, filtros: any) {
    let url = this._api_host + this._api_prefix + 'usuario?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.institucion !== 'undefined' && filtros.institucion !== null && filtros.institucion !== '' ) {
      url += '&institucion=' + filtros.institucion;
    }
    return this.http.get(url);
  }

  /*** Recepciones ***/

  getRecepciones(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Recepciones...');
    let url = '';
    //let url = this._api_host + '/api/recepcion/' + this.authService.getSesion().id + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    //let url = 'http://localhost/segpres/json.php?servicio=recepciones&inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.estado !== 'undefined' && filtros.estado !== null && filtros.estado !== '' ) {
      url += '&estado=' + filtros.estado;
    }
    if ( typeof filtros.fechaInicio !== 'undefined' && filtros.fechaInicio !== null && filtros.fechaInicio !== '' ) {
      url += '&fechaInicio=' + filtros.fechaInicio + ' 00:00:00';
    }
    if ( typeof filtros.fechaTermino !== 'undefined' && filtros.fechaTermino !== null && filtros.fechaTermino !== '' ) {
      url += '&fechaTermino=' + filtros.fechaTermino + ' 00:00:00';
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }
}
