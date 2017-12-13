import { Component, OnInit } from '@angular/core';
import { NgbPaginationConfig, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap';
import { NgForm } from '@angular/forms';
import { BsDatepickerConfig } from 'ngx-bootstrap/datepicker';
import { listLocales } from 'ngx-bootstrap/bs-moment';
import { AuthService } from '../auth/auth.service';
import { ApiService } from '../services/api.service';
import { AlertasService } from '../services/alertas.service';
import { Alerta } from '../models/alerta.model';
import { Recepcion } from '../models/recepcion.model';
import { Institucion } from '../models/institucion.model';
import { Ministerio } from '../models/ministerio.model';
import { Subsecretaria } from '../models/subsecretaria.model';
import { Buscador } from '../models/buscador.model';
import {FechaPipe} from '../pipes/fecha.pipe';


@Component({
  selector: 'app-recepciones',
  templateUrl: './recepciones.component.html',
  styleUrls: ['./recepciones.component.css'],
  providers: [NgbPaginationConfig]
})
export class RecepcionesComponent implements OnInit {
  public _component_entidad: string = 'recepcion';

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');
  public _buscador_ffi: Date;
  public _buscador_fft: Date;

  public _lista_ministerios: Ministerio[] = [];
  public _lista_subsecretarias: Subsecretaria[] = [];
  public _lista_instituciones: Institucion[] = [];
  public _items: Recepcion[] = [];

  public maxDate = new Date();
  locale = 'es';
  locales = listLocales();
  bsConfig: Partial<BsDatepickerConfig>;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, public authService: AuthService, configNgbPagination: NgbPaginationConfig, private _fechaPipe: FechaPipe) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;

    this.bsConfig = Object.assign({}, {locale: this.locale, containerClass: 'theme-blue', showWeekNumbers: false, dateInputFormat: 'DD/MM/Y'});
  }

  getListas() {
    this._apiService.getLista('ministerio')
      .subscribe(
        (data: any[]) => {
          this._lista_ministerios = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_ministerios.push( new Ministerio(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de ministerios'})),
        () => this._apiService.getLista('subsecretaria')
          .subscribe(
            (data: any[]) => {
              this._lista_subsecretarias = [];
              if ( data.length > 0 ) {
                for (const d of data) {
                  this._lista_subsecretarias.push( new Subsecretaria(d) );
                }
              }
            },
            (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de subsecretarías'})),
            () => this._apiService.getLista('institucion')
              .subscribe(
                (data: any[]) => {
                  this._lista_instituciones = [];
                  if ( data.length > 0 ) {
                    for (const d of data) {
                      this._lista_instituciones.push( new Institucion(d) );
                    }
                  }
                },
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de instituciones'})),
                () => this.updateItems()
              )
          )
      );
  }

  updateItems() {
    this._apiService.loadBuscador(this._component_entidad + '.buscador')
      .subscribe(
        (data: any[]) => {
          this._items = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._items.push( new Recepcion(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar las recepciones'}))
      );
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

  onPaginadorCambia (buscador: Buscador) {
    this._buscador.paginador = buscador.paginador;
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  ngOnInit() {
    this.getListas()

    if ( typeof this._buscador.filtro.ministerio_id === 'undefined' || this._buscador.filtro.ministerio_id === null ) {
      this._buscador.filtro.ministerio_id = '';
    }
    if ( typeof this._buscador.filtro.subsecretaria_id === 'undefined' || this._buscador.filtro.subsecretaria_id === null ) {
      this._buscador.filtro.subsecretaria_id = '';
    }
    if ( typeof this._buscador.filtro.institucion_id === 'undefined' || this._buscador.filtro.institucion_id === null ) {
      this._buscador.filtro.institucion_id = '';
    }
    if ( typeof this._buscador.filtro.estado === 'undefined' || this._buscador.filtro.estado === null ) {
      this._buscador.filtro.estado = '';
    }

    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
  }

}
