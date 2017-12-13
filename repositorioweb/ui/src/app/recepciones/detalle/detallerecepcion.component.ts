import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Detallerecepcion } from '../../models/detallerecepcion.model';
import { BsDatepickerConfig } from 'ngx-bootstrap/datepicker';
import { listLocales } from 'ngx-bootstrap/bs-moment';
import { Buscador } from '../../models/buscador.model';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { NgForm } from '@angular/forms';
import { Alerta } from '../../models/alerta.model';
import {FechaPipe} from '../../pipes/fecha.pipe';

@Component({
  selector: 'app-detallerecepcion',
  templateUrl: './detallerecepcion.component.html',
  providers: []
})
export class DetallerecepcionComponent implements OnInit {
  public _component_entidad: string = 'recepcion/detalle';
  public _component_entidad_id: number;

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');
  public _buscador_ffi: Date;
  public _buscador_fft: Date;

  public _items: Detallerecepcion[] = [];

  public maxDate = new Date();
  locale = 'es';
  locales = listLocales();
  bsConfig: Partial<BsDatepickerConfig>;

  constructor(private route: ActivatedRoute, private _apiService: ApiService, private _alertasService: AlertasService, configNgbPagination: NgbPaginationConfig, private _fechaPipe: FechaPipe) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;

    this.bsConfig = Object.assign({}, {locale: this.locale, containerClass: 'theme-blue', showWeekNumbers: false, dateInputFormat: 'DD/MM/Y'});
  }

  updateItems() {
    // entidad
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._buscador.entidad = this._component_entidad + '/' + this._component_entidad_id;
      this._apiService.loadBuscador(this._component_entidad + '.buscador')
        .subscribe(
          (data: any[]) => {
            this._items = [];
            if (data.length > 0) {
              for (const d of data) {
                this._items.push(new Detallerecepcion(d));
              }
            }
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({
            type: 'danger',
            dismissible: true,
            istimeout: true,
            timeout: 10000,
            msg: 'Error al buscar los detalles'
          }))
        );
    }
  }

  onPaginadorCambia (buscador: Buscador) {
    this._buscador.paginador = buscador.paginador;
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  onFiltroSubmit (frm: NgForm) {
    this._buscador.filtro.fecha_inicio = '';
    if ( typeof(this._buscador_ffi) !== 'undefined' && this._buscador_ffi !== null ) {
      this._buscador.filtro.fecha_inicio = this._fechaPipe.transform(this._buscador_ffi) + ' 00:00:00';
      // this._buscador.filtro.fecha_inicio = this.getFecha( this._buscador_ffi ) + ' 00:00:00';
    }
    this._buscador.filtro.fecha_termino = '';
    if ( typeof(this._buscador_fft) !== 'undefined' && this._buscador_fft !== null ) {
      this._buscador.filtro.fecha_termino = this._fechaPipe.transform(this._buscador_fft) + ' 00:00:00';
      // this._buscador.filtro.fecha_termino = this.getFecha( this._buscador_fft ) + ' 00:00:00';
    }
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  ngOnInit(){
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.updateItems();
  }
}
