import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { Paginacion } from './models/paginacion.model';
import { AuthService } from './auth/auth.service';

@Injectable()
export class BackendService {

  private loggedIn = false;

  constructor(private http: Http, private authService: AuthService) { }

  getAPI(){
    // return 'https://54.187.41.133:8080';
    // return window.location.protocol + '//' + window.location.hostname + ':' + window.location.port;
    return '';
  }

  logIn() {
    console.log('Login Developer');
    return this.http.get(this.getAPI() + '/callback/1');
  }

  create(entidad: string, data: any) {
    const headers = new Headers({'Content-Type': 'application/json'});
    return this.http.post(this.getAPI() + '/api/' + entidad, JSON.stringify(data), {headers: headers} );
  }

  update(entidad: string, data: any) {
    /*if ( data.id === 0 ) {
      return this.create(entidad, data);
    }*/
    const headers = new Headers({'Content-Type': 'application/json'});
    return this.http.put(this.getAPI() + '/api/' + entidad, JSON.stringify(data), {headers: headers} );
  }

  delete(entidad: string) {
    return this.http.delete(this.getAPI() + '/api/' + entidad);
  }

  getLista(entidad: string) {
    return this.http.get(this.getAPI() + '/api/' + entidad + '?cuantos=0&orden=nombre&tipoOrden=asc&habilitado=true' );
    // return this.http.get('http://localhost/segpres/json.php?servicio=' + entidad + '&cuantos=0&orden=nombre&tipoOrden=asc&habilitado=true' );
  }

  getListaMinisterios() {
    return this.getLista('ministerio');
  }

  getListaSubsecretarias() {
    return this.getLista('subsecretaria');
  }

  getListaInstituciones() {
    return this.getLista('institucion');
  }

  getListaReceptores() {
    return this.getLista('recepcion/receptores');
  }

  getUsuarios(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Usuarios...');
    let url = this.getAPI() + '/api/usuario?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    // let url = 'http://localhost/segpres/json.php?servicio=usuarios&inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.ministerio !== 'undefined' && filtros.ministerio !== null && filtros.ministerio !== '0' ) {
      url += '&ministerio_id=' + filtros.ministerio;
    }
    if ( typeof filtros.subsecretaria !== 'undefined' && filtros.subsecretaria !== null && filtros.subsecretaria !== '0' ) {
      url += '&subsecretaria_id=' + filtros.subsecretaria;
    }
    if ( typeof filtros.institucion !== 'undefined' && filtros.institucion !== null && filtros.institucion !== '0' ) {
      url += '&institucion_id=' + filtros.institucion;
    }
    if ( typeof filtros.rut !== 'undefined' && filtros.rut !== null && filtros.rut !== '' ) {
      url += '&rut=' + filtros.rut;
    }
    if ( typeof filtros.tipo !== 'undefined' && filtros.tipo !== null && filtros.tipo !== '' ) {
      url += '&tipo=' + filtros.tipo;
    }
    if ( typeof filtros.rol !== 'undefined' && filtros.rol !== null && filtros.rol !== '' ) {
      url += '&rol=' + filtros.rol;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getUsuario(id: number) {
    console.log('Cargando Usuario...');
    let url = this.getAPI() + '/api/usuario?id=' + id;
    //let url = 'http://localhost/segpres/json.php?servicio=usuario';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getInstituciones(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Instituciones...');
    let url = this.getAPI() + '/api/institucion?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.ministerio !== 'undefined' && filtros.ministerio !== null && filtros.ministerio !== '' ) {
      url += '&ministerio_id=' + filtros.ministerio;
    }
    if ( typeof filtros.subsecretaria !== 'undefined' && filtros.subsecretaria !== null && filtros.subsecretaria !== '' ) {
      url += '&subsecretaria_id=' + filtros.subsecretaria;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getInstitucion(id: number) {
    console.log('Cargando Institución...');
    let url = this.getAPI() + '/api/institucion?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=usuario';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getRecepciones(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Recepciones...');
    let url = this.getAPI() + '/api/recepcion?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    // let url = this.getAPI() + '/api/recepcion/' + this.authService.getSesion().id + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    // let url = 'http://localhost/segpres/json.php?servicio=recepciones&inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.ministerio !== 'undefined' && filtros.ministerio !== null && filtros.ministerio !== '0' ) {
      url += '&ministerio_id=' + filtros.ministerio;
    }
    if ( typeof filtros.subsecretaria !== 'undefined' && filtros.subsecretaria !== null && filtros.subsecretaria !== '0' ) {
      url += '&subsecretaria_id=' + filtros.subsecretaria;
    }
    if ( typeof filtros.institucion !== 'undefined' && filtros.institucion !== null && filtros.institucion !== '0' ) {
      url += '&institucion_id=' + filtros.institucion;
    }
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

  getRecepcionBitacoras(paginacion: Paginacion, filtros: any){ // (id_recepcion: number, inicio: number, porpagina:number, estado: string, fechaInicio: string, fechaTermino: string) {
    console.log('Cargando Bitácora Recepción...');
    const idRecepcion = ( typeof filtros.idRecepcion !== 'undefined' && filtros.idRecepcion !== null && filtros.idRecepcion !== 0 ) ? filtros.idRecepcion : 0;
    let url = this.getAPI() + '/api/recepcion/bitacora/' + idRecepcion + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    if ( typeof filtros.nivel !== 'undefined' && filtros.nivel !== null && filtros.nivel !== '' ) {
      url += '&nivel=' + filtros.nivel;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getRecepcionDetalles(paginacion: Paginacion, filtros: any){ // (id_recepcion: number, inicio: number, porpagina:number, fechaInicio: string, fechaTermino: string ) {
    console.log('Cargando Detalle Recepción...');
    const idRecepcion = ( typeof filtros.idRecepcion !== 'undefined' && filtros.idRecepcion !== null && filtros.idRecepcion !== 0 ) ? filtros.idRecepcion : 0;
    let url = this.getAPI() + '/api/recepcion/detalle/' + idRecepcion + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    if ( typeof filtros.fechaInicio !== 'undefined' && filtros.fechaInicio !== null && filtros.fechaInicio !== '' ) {
      url += '&fechaInicio=' + filtros.fechaInicio;
    }
    if ( typeof filtros.fechaTermino !== 'undefined' && filtros.fechaTermino !== null && filtros.fechaTermino !== '' ) {
      url += '&fechaTermino=' + filtros.fechaTermino;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getRecepcionFactores(paginacion: Paginacion, filtros: any){
    console.log('Cargando Factores Recepción...');
    const idRecepcion = ( typeof filtros.idRecepcion !== 'undefined' && filtros.idRecepcion !== null && filtros.idRecepcion !== 0 ) ? filtros.idRecepcion : 0;
    let url = this.getAPI() + '/api/recepcion/factores/' + idRecepcion + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getMinisterios(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Ministerios...');
    let url = this.getAPI() + '/api/ministerio?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.id_cha !== 'undefined' && filtros.id_cha !== null && filtros.id_cha !== '' ) {
      url += '&id_cha=' + filtros.id_cha;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getMinisterio(id: number) {
    console.log('Cargando Ministerio...');
    let url = this.getAPI() + '/api/ministerio?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=receptor';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getSubsecretarias(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Subsecretarías...');
    let url = this.getAPI() + '/api/subsecretaria?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.ministerio !== 'undefined' && filtros.ministerio !== null && filtros.ministerio !== '' ) {
      url += '&ministerio_id=' + filtros.ministerio;
    }
    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getSubsecretaria(id: number) {
    console.log('Cargando Subsecretaría...');
    let url = this.getAPI() + '/api/subsecretaria?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=receptor';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getTramites(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Trámites...');
    let url = this.getAPI() + '/api/tipotramite?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.institucion !== 'undefined' && filtros.institucion !== null && filtros.institucion !== '' ) {
      url += '&institucion_id=' + filtros.institucion;
    }
    if ( typeof filtros.codigo_pmg !== 'undefined' && filtros.codigo_pmg !== null && filtros.codigo_pmg !== '' ) {
      url += '&codigo_pmg=' + filtros.codigo_pmg;
    }
    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getTramite(id: number) {
    console.log('Cargando Trámite...');
    let url = this.getAPI() + '/api/tipotramite?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=receptor';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getTramiteFactores(id: number) {
    console.log('Cargando Factores Trámite...');
    let url = this.getAPI() + '/api/tipotramite/factor/' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=receptor';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getTramiteFactoresHistoricos(id: number, paginacion: Paginacion, filtros: any) {
    console.log('Cargando Factores Históricos...');
    let url = this.getAPI() + '/api/tipotramite/historico/' + id + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.fechaInicio !== 'undefined' && filtros.fechaInicio !== null && filtros.fechaInicio !== '' ) {
      url += '&fecha_creacion_inicio=' + filtros.fechaInicio;
    }
    if ( typeof filtros.fechaTermino !== 'undefined' && filtros.fechaTermino !== null && filtros.fechaTermino !== '' ) {
      url += '&fecha_creacion_termino=' + filtros.fechaTermino;
    }
    if ( typeof filtros.canal !== 'undefined' && filtros.canal !== null && filtros.canal !== '' ) {
      url += '&canal_id=' + filtros.canal;
    }

    console.log('url: ' + url);
    return this.http.get(url);
  }

  getReceptores(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Receptores...');
    let url = this.getAPI() + '/api/receptor?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.canal !== 'undefined' && filtros.canal !== null && filtros.canal !== '' ) {
      url += '&canal_transmision_id=' + filtros.canal;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getReceptor(id: number) {
    console.log('Cargando Receptor...');
    let url = this.getAPI() + '/api/receptor?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=receptor';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getReceptorMascaras(id: number) {
    console.log('Cargando Máscaras Receptor...');
    let url = this.getAPI() + '/api/receptor/mascara/' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=receptor';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getPlantillas(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Plantillas...');
    let url = this.getAPI() + '/api/plantilla?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getPlantilla(id: number) {
    console.log('Cargando Plantilla...');
    let url = this.getAPI() + '/api/plantilla?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=mantenedoritem';
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getMantenedores() {
    return this.http.get(this.getAPI() + '/api/mantenedor' );
    // return this.http.get('http://localhost/segpres/json.php?servicio=mantenedor' );
  }

  getMantenedoresItems(paginacion: Paginacion, filtros: any) {
    console.log('Cargando Mantenedores...');
    let url = this.getAPI() + '/api/mantenedor/' + filtros.tabla + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    // let url = this.getAPI() + '/api/recepcion/' + this.authService.getSesion().id + '?inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;
    // let url = 'http://localhost/segpres/json.php?servicio=recepciones&inicio=' + paginacion.inicio + '&cuantos=' + paginacion.porpagina + '&orden=' + paginacion.orden + '&tipoOrden=' + paginacion.direccion;

    if ( typeof filtros.nombre !== 'undefined' && filtros.nombre !== null && filtros.nombre !== '' ) {
      url += '&nombre=' + filtros.nombre;
    }
    if ( typeof filtros.habilitado !== 'undefined' && filtros.habilitado !== null && filtros.habilitado !== '' ) {
      url += '&habilitado=' + filtros.habilitado;
    }
    console.log('url: ' + url);
    return this.http.get(url);
  }

  getMantenedorItem(tabla: string, id: number) {
    console.log('Cargando Mantenedor...');
    let url = this.getAPI() + '/api/mantenedor/' + tabla + '?id=' + id;
    // let url = 'http://localhost/segpres/json.php?servicio=mantenedoritem';
    console.log('url: ' + url);
    return this.http.get(url);
  }

}
