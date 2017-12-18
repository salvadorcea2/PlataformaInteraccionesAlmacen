import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { Institucion } from '../../models/institucion.model';
import { Servicio } from '../../models/servicio.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';

@Component({
    selector: 'app-serviciover',
    templateUrl: './serviciover.component.html',
    styleUrls: ['./serviciover.component.css']
})
export class ServicioverComponent implements OnInit {

    public _component_entidad: string = 'servicio';
    public _component_entidad_id: number;

    public _lista_ministerios: Ministerio[] = [];
    public _lista_subsecretarias: Subsecretaria[] = [];
    public _lista_instituciones: Institucion[] = [];
    public item: Servicio = new Servicio({});
    public modalRef: BsModalRef;

    constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute, private modalService: BsModalService) { }

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
                                () => this.updateItem()
                            )
                    )
            );
    }

    updateItem() {
        if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
            this._apiService.load(this._component_entidad, this._component_entidad_id)
                .subscribe(
                    (data: any) => {
                        this.item = new Servicio(data.pop());
                    },
                    (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el servicio ID: ' + this._component_entidad_id}))
                );
        }
    }

    onEliminar (template: TemplateRef<any>) {
        this.modalRef = this.modalService.show(template);
    }

    _onEliminar () {
        this.modalRef.hide();
        this._apiService.borrar(this._component_entidad + '/' + this._component_entidad_id)
            .subscribe(
                (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el servicio "' + this.item.nombre + '" con éxito'})),
                (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar el servicio "' + this.item.nombre + '"'})),
                () => {
                    this.router.navigate(['/servicios']);
                    this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó el servicio "' + this.item.nombre + '" con éxito'}));
                }
            );
    }

    ngOnInit() {
        this._component_entidad_id = this.route.snapshot.params['id'];
        this.getListas();
    }
}
