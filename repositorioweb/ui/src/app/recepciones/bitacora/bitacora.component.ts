import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Bitacora } from '../../models/bitacora.model';
import { Buscador } from '../../models/buscador.model';
import { Alerta } from '../../models/alerta.model';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-bitacora',
  templateUrl: './bitacora.component.html',
  providers: []
})
export class BitacoraComponent implements OnInit {
  public _component_entidad: string = 'recepcion/bitacora';
  public _component_entidad_id: number;

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');

  public _items: Bitacora[] = [];

  constructor(private route: ActivatedRoute, private _apiService: ApiService, private _alertasService: AlertasService, configNgbPagination: NgbPaginationConfig) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;
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
                this._items.push(new Bitacora(d));
              }
            }
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({
            type: 'danger',
            dismissible: true,
            istimeout: true,
            timeout: 10000,
            msg: 'Error al buscar la bitacora'
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

    if ( typeof this._buscador.filtro.nivel === 'undefined' || this._buscador.filtro.nivel === null ) {
      this._buscador.filtro.nivel = '';
    }

    this.updateItems();
  }
}
