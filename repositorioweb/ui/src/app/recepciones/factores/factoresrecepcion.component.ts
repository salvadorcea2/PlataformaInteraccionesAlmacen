import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Factorrecepcion } from '../../models/recepcion.model';
import {Tramite} from "../../models/tramite.model";
import {MantenedorItem} from "../../models/mantenedor.model";
import {Buscador} from "../../models/buscador.model";
import {ApiService} from "../../services/api.service";
import {AlertasService} from "../../services/alertas.service";
import {NgForm} from "@angular/forms";
import {Alerta} from "../../models/alerta.model";

@Component({
  selector: 'app-factoresrecepcion',
  templateUrl: './factoresrecepcion.component.html',
  providers: []
})
export class FactoresrecepcionComponent implements OnInit {
  public _component_entidad: string = 'recepcion/factores';
  public _component_entidad_id: number;

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');

  public _lista_tramites: Tramite[] = [];
  public _lista_tipo_interacciones: MantenedorItem[] = [];
  public _items: Factorrecepcion[] = [];

  constructor(private route: ActivatedRoute, private _apiService: ApiService, private _alertasService: AlertasService, configNgbPagination: NgbPaginationConfig) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;
  }

  getListas() {
    this._apiService.getLista('tipotramite')
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
        () => this._apiService.getLista('mantenedor/tipo_interaccion')
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
          )
      );
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
                this._items.push(new Factorrecepcion(d));
              }
            }
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({
            type: 'danger',
            dismissible: true,
            istimeout: true,
            timeout: 10000,
            msg: 'Error al buscar los factores'
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
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  ngOnInit(){
    this._component_entidad_id = this.route.snapshot.params['id'];

    if ( typeof this._buscador.filtro.tipo_interaccion_id === 'undefined' || this._buscador.filtro.tipo_interaccion_id === null ) {
      this._buscador.filtro.tipo_interaccion_id = '';
    }

    this.getListas();
  }
}
