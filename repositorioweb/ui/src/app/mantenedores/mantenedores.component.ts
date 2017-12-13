import { Component, OnInit, TemplateRef } from '@angular/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Mantenedor, MantenedorItem } from '../models/mantenedor.model';
import { NgForm } from '@angular/forms';
import { Buscador } from '../models/buscador.model';
import { ApiService } from '../services/api.service';
import { FiltroPipe } from '../pipes/filtro.pipe';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { AlertasService } from '../services/alertas.service';
import { Alerta } from '../models/alerta.model';

@Component({
  selector: 'app-mantenedores',
  templateUrl: './mantenedores.component.html',
  styleUrls: ['./mantenedores.component.css']
})
export class MantenedoresComponent implements OnInit {

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private _filtroPipe: FiltroPipe, configNgbPagination: NgbPaginationConfig, private modalService: BsModalService) {
    configNgbPagination.size = 'sm';
    configNgbPagination.boundaryLinks = true;
    configNgbPagination.maxSize = 5;
    configNgbPagination.ellipses = false;
    configNgbPagination.rotate = true;
  }

  public _component_entidad: string = 'mantenedor';

  public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');
  public _lista_mantenedores: Mantenedor[] = [];
  public _items: MantenedorItem[] = [];
  public _item_eliminar: MantenedorItem = new MantenedorItem({});
  public modalRef: BsModalRef;

  getListas() {
    this._apiService.getLista('mantenedor', {orden: 'tabla'})
      .subscribe(
        (data: any[]) => {
          this._lista_mantenedores = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_mantenedores.push( new Mantenedor(d) );
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de mantenedores'})),
        () => this.updateItems()
      );
  }

  updateItems() {
    if ( this._buscador.filtro.tabla !== '' ) {
      this._apiService.loadBuscador(this._component_entidad + '.buscador')
        .subscribe(
          (data: any[]) => {
            this._items = [];
            if (data.length > 0) {
              for (const d of data) {
                this._items.push(new MantenedorItem(d));
              }
            }
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({
            type: 'danger',
            dismissible: true,
            istimeout: true,
            timeout: 10000,
            msg: 'Error al buscar los mantenedores'
          }))
        );
    }
  }

  onFiltroSubmit (frm: NgForm) {
    this._buscador.filtro = this._filtroPipe.transform(frm);
    this._buscador.entidad = this._component_entidad + '/' + this._buscador.filtro.tabla;
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  onPaginadorCambia (buscador: Buscador) {
    this._buscador.paginador = buscador.paginador;
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    this.updateItems();
  }

  onEliminar (template: TemplateRef<any>, item: MantenedorItem) {
    this._item_eliminar = item;
    this.modalRef = this.modalService.show(template);
  }

  _onEliminar () {
    this.modalRef.hide();
    this._apiService.borrar(this._component_entidad + '/' + this._buscador.filtro.tabla + '/' + this._item_eliminar.id)
      .subscribe(
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el mantenedor "' + this._item_eliminar.nombre + '" con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar el mantenedor "' + this._item_eliminar.nombre + '"'})),
        () => {
          this._item_eliminar = new MantenedorItem({});
          this.updateItems();
        }
      );
  }

  ngOnInit() {
    this.getListas();
    if ( typeof this._buscador.filtro.tabla === 'undefined' || this._buscador.filtro.tabla === null ) {
      this._buscador.filtro.tabla = '';
    }
    if ( typeof this._buscador.filtro.habilitado === 'undefined' || this._buscador.filtro.habilitado === null ) {
      this._buscador.filtro.habilitado = '';
    }
    this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
  }
}
