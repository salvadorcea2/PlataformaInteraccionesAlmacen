import { Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { BsDatepickerConfig } from 'ngx-bootstrap/datepicker';
import { listLocales } from 'ngx-bootstrap/bs-moment';
import { AuthService } from '../../auth/auth.service';
import { Tramite, TramiteFactorHistorico } from '../../models/tramite.model';
import { NgForm } from '@angular/forms';
import { MantenedorItem } from '../../models/mantenedor.model';
import { ActivatedRoute } from '@angular/router';
import {Buscador} from "../../models/buscador.model";
import {ApiService} from '../../services/api.service';
import {AlertasService} from '../../services/alertas.service';
import {Alerta} from '../../models/alerta.model';
import {FechaPipe} from '../../pipes/fecha.pipe';

@Component({
  selector: 'app-tramitehistorico',
  templateUrl: './tramitehistorico.component.html',
  styleUrls: ['./tramitehistorico.component.css']
})
export class TramitehistoricoComponent implements OnInit {
  public _component_entidad: string = 'tipotramite';
  public _component_entidad_id: number;

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '/historico.buscador');
  public _buscador_ffi: Date;
  public _buscador_fft: Date;

  public _parent: Tramite = new Tramite({});
  public _lista_tramites: Tramite[] = [];
  public _lista_tipo_interacciones: MantenedorItem[] = [];
  public _items: TramiteFactorHistorico[] = [];

  public maxDate = new Date();
  locale = 'es';
  locales = listLocales();
  bsConfig: Partial<BsDatepickerConfig>;

  constructor(private route: ActivatedRoute, private _apiService: ApiService, private _alertasService: AlertasService, public authService: AuthService, configNgbPagination: NgbPaginationConfig, private _fechaPipe: FechaPipe) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;

    this.bsConfig = Object.assign({}, {locale: this.locale, containerClass: 'theme-blue', showWeekNumbers: false, dateInputFormat: 'DD/MM/Y'});
  }

  getListas() {
    /*this._apiService.getLista('tipotramite')
      .subscribe(
        (data: any[]) => {
          this._lista_tramites = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_tramites.push( new Tramite(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de trámites'})),
        () => */this._apiService.getLista('mantenedor/tipo_interaccion')
          .subscribe(
            (data: any[]) => {
              this._lista_tipo_interacciones = [];
              if ( data.length > 0 ) {
                for (const d of data) {
                  this._lista_tipo_interacciones.push( new MantenedorItem(d) );
                }
              }
            },
            (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de tipo de interacciones'})),
            () => this.updateItems()
          // )
      );
  }

  updateItems() {
    // entidad
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._buscador.entidad = this._component_entidad + '/historico/' + this._component_entidad_id;
      this._apiService.setBuscador(this._component_entidad + '/historico.buscador', this._buscador);
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this._parent = new Tramite(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el trámite ID: ' + this._component_entidad_id})),
          () => this._apiService.loadBuscador(this._component_entidad + '/historico.buscador')
            .subscribe(
              (data: any[]) => {
                this._items = [];
                if (data.length > 0) {
                  for (const d of data) {
                    this._items.push(new TramiteFactorHistorico(d));
                  }
                }
              },
              (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({
                type: 'danger',
                dismissible: true,
                istimeout: true,
                timeout: 10000,
                msg: 'Error al buscar los factores históricos'
              }))
            )
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
      this._buscador.filtro.fecha_termino = this._fechaPipe.transform(this._buscador_fft) + ' 23:59:59';
      // this._buscador.filtro.fecha_termino = this.getFecha( this._buscador_fft ) + ' 00:00:00';
    }
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);

    this.updateItems();
  }

  ngOnInit(){
    this._component_entidad_id = +this.route.snapshot.params['id'];

    if ( typeof this._buscador.filtro.tipo_interaccion_id === 'undefined' || this._buscador.filtro.tipo_interaccion_id === null ) {
      this._buscador.filtro.tipo_interaccion_id = '';
    }

    this.getListas();
  }
}
