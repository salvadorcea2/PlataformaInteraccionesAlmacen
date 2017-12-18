import { Component, OnInit, TemplateRef } from '@angular/core';
import { NgbPaginationConfig } from '@ng-bootstrap/ng-bootstrap';
import { Servicio } from '../models/servicio.model';
import { NgForm } from '@angular/forms';
import { Buscador } from '../models/buscador.model';
import { ApiService } from '../services/api.service';
import { BsModalRef, BsModalService } from 'ngx-bootstrap';
import { AlertasService } from '../services/alertas.service';
import { Alerta } from '../models/alerta.model';
import { Subsecretaria } from '../models/subsecretaria.model';
import { Institucion } from '../models/institucion.model';
import { Ministerio } from '../models/ministerio.model';

@Component({
    selector: 'app-servicios',
    templateUrl: './servicios.component.html',
    styleUrls: ['./servicios.component.css']
})
export class ServiciosComponent implements OnInit {

    constructor(private _apiService: ApiService, private _alertasService: AlertasService, configNgbPagination: NgbPaginationConfig, private modalService: BsModalService) {
        configNgbPagination.size = 'sm';
        configNgbPagination.boundaryLinks = true;
        configNgbPagination.maxSize = 5;
        configNgbPagination.ellipses = false;
        configNgbPagination.rotate = true;
    }

    public _component_entidad: string = 'servicio';

    public _buscador: Buscador = this._apiService.getBuscador(this._component_entidad + '.buscador');
    public _lista_ministerios: Ministerio[] = [];
    public _lista_subsecretarias: Subsecretaria[] = [];
    public _lista_instituciones: Institucion[] = [];
    public _items: Servicio[] = [];
    public _item_eliminar: Servicio = new Servicio({});
    public modalRef: BsModalRef;

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
                            this._items.push( new Servicio(d) );
                        }
                    }
                },
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al buscar los servicios'}))
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

    onEliminar (template: TemplateRef<any>, item: Servicio) {
        this._item_eliminar = item;
        this.modalRef = this.modalService.show(template);
    }

    _onEliminar () {
        this.modalRef.hide();
        this._apiService.borrar(this._component_entidad + '/' + this._item_eliminar.id)
            .subscribe(
                (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el servicio "' + this._item_eliminar.nombre + '" con éxito'})),
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar el servicio "' + this._item_eliminar.nombre + '"'})),
                () => {
                    this._item_eliminar = new Servicio({});
                    this.updateItems();
                }
            );
    }

    ngOnInit() {
        this.getListas();
        if ( typeof this._buscador.filtro.ministerio_id === 'undefined' || this._buscador.filtro.ministerio_id === null ) {
            this._buscador.filtro.ministerio_id = '';
        }
        if ( typeof this._buscador.filtro.subsecretaria_id === 'undefined' || this._buscador.filtro.subsecretaria_id === null ) {
            this._buscador.filtro.subsecretaria_id = '';
        }
        if ( typeof this._buscador.filtro.institucion_id === 'undefined' || this._buscador.filtro.institucion_id === null ) {
            this._buscador.filtro.institucion_id = '';
        }

        if ( typeof this._buscador.filtro.habilitado === 'undefined' || this._buscador.filtro.habilitado === null ) {
            this._buscador.filtro.habilitado = '';
        }
        this._apiService.setBuscador(this._component_entidad + '.buscador', this._buscador);
    }
}