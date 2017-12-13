import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Mantenedor, MantenedorItem } from '../../models/mantenedor.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-mantenedoreditar',
  templateUrl: './mantenedoreditar.component.html',
  styleUrls: ['./mantenedoreditar.component.css']
})
export class MantenedoreditarComponent implements OnInit {

  public _component_entidad: string = 'mantenedor';
  public _component_entidad_id: number;
  public _component_entidad_tabla: string;

  public _lista_mantenedores: Mantenedor[] = [];
  public mantenedor: Mantenedor = new Mantenedor({});
  public item: MantenedorItem = new MantenedorItem({});
  public modalRef: BsModalRef;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute, private modalService: BsModalService) { }

  getListas() {
    this._apiService.getLista('mantenedor', {orden: 'tabla'})
      .subscribe(
        (data: any[]) => {
          this._lista_mantenedores = [];
          if ( data.length > 0 ) {
            for (const d of data) {
              this._lista_mantenedores.push( new Mantenedor(d) );
              if ( d.tabla === this._component_entidad_tabla ) {
                this.mantenedor = new Mantenedor(d);
              }
            }
          }
        },
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar lista de mantenedores'})),
        () => this.updateItem()
      );
  }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null ) {
      this._apiService.load(this._component_entidad + '/' + this._component_entidad_tabla, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new MantenedorItem(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar el mantenedor ID: ' + this._component_entidad_id}))
        );
    }
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }

    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._apiService.crear(this._component_entidad + '/' + this._component_entidad_tabla, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/mantenedores', this._component_entidad_tabla, Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad + '/' + this._component_entidad_tabla, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/mantenedores', this._component_entidad_tabla, Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this._component_entidad_tabla = this.route.snapshot.params['tabla'];
    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._component_entidad_tabla = '';
      this.mantenedor = new Mantenedor({tabla: '', singular: 'Mantenedor', plural: 'Mantenedores'});
    }
    this.getListas();
  }
}
