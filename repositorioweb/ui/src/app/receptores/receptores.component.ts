import { Component, OnInit, TemplateRef } from '@angular/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Receptor } from '../models/receptor.model';
import { MantenedorItem } from '../models/mantenedor.model';
import { NgForm } from '@angular/forms';
import { Buscador } from '../models/buscador.model';
import { ApiService } from '../services/api.service';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { AlertasService } from '../services/alertas.service';
import { Alerta } from '../models/alerta.model';

@Component({
  selector: 'app-receptores',
  templateUrl: './receptores.component.html',
  styleUrls: ['./receptores.component.css']
})
export class ReceptoresComponent implements OnInit {

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, configNgbPagination: NgbPaginationConfig, private modalService: BsModalService) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;
  }

  public _component_entidad: string = 'receptor';

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');
  public _lista_canales: MantenedorItem[] = [];
  public _lista_formatos: MantenedorItem[] = [];
  public _items: Receptor[] = [];
  public _item_eliminar: Receptor = new Receptor({});
  public modalRef: BsModalRef;

  getListas() {
    this._apiService.getLista('mantenedor/canal_transmision')
      .subscribe(
        (data: any[]) => {
          this._lista_canales = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_canales.push( new MantenedorItem(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de canales'})),
        () => this._apiService.getLista('mantenedor/formato')
          .subscribe(
            (data: any[]) => {
              this._lista_formatos = [];
              if ( data.length > 0 ) {
                for (const d of data) {
                  this._lista_formatos.push( new MantenedorItem(d) );
                }
              }
            },
            (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de formatos'})),
            () => this.updateItems()
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
              this._items.push( new Receptor(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar los receptores'}))
      );
  }

  onFiltroSubmit (frm: NgForm) {
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  onPaginadorCambia (buscador: Buscador) {
    this._buscador.paginador = buscador.paginador;
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  onEliminar (template: TemplateRef<any>, item: Receptor) {
    this._item_eliminar = item;
    this.modalRef = this.modalService.show(template);
  }

  _onEliminar () {
    this.modalRef.hide();
    this._apiService.borrar(this._component_entidad + '/' + this._item_eliminar.id)
      .subscribe(
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el receptor "' + this._item_eliminar.nombre + '" con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar el receptor "' + this._item_eliminar.nombre + '"'})),
        () => {
          this._item_eliminar = new Receptor({});
          this.updateItems();
        }
      );
  }

  ngOnInit() {
    this.getListas();
    if ( typeof this._buscador.filtro.canal_transmision_id === 'undefined' || this._buscador.filtro.canal_transmision_id === null ) {
      this._buscador.filtro.canal_transmision_id = '';
    }
    if ( typeof this._buscador.filtro.habilitado === 'undefined' || this._buscador.filtro.habilitado === null ) {
      this._buscador.filtro.habilitado = '';
    }
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
  }
}
