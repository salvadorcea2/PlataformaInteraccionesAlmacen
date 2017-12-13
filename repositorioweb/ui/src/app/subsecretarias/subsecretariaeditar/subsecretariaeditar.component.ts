import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-subsecretariaeditar',
  templateUrl: './subsecretariaeditar.component.html',
  styleUrls: ['./subsecretariaeditar.component.css']
})
export class SubsecretariaeditarComponent implements OnInit {

  public _component_entidad: string = 'subsecretaria';
  public _component_entidad_id: number;

  public _lista_ministerios: Ministerio[] = [];
  public item: Subsecretaria = new Subsecretaria({});
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
        () => this.updateItem()
      );
  }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Subsecretaria(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar la subsecretaría ID: ' + this._component_entidad_id}))
        );
    }
  }

  onSubmit(frm: NgForm){
    if ( !frm.valid ) {
      this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error en el formulario, favor revisa nuevamente'}));
      return;
    }
    this.item.ministerio_id = +this.item.ministerio_id;
    if ( typeof this._component_entidad_id === 'undefined' || this._component_entidad_id === null ) {
      this._apiService.crear(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/subsecretarias', Number(this._component_entidad_id), 'ver'])
        );
    }else {
      this._apiService.actualizar(this._component_entidad, this.item)
        .subscribe(
          (data: any) => {
            this._component_entidad_id = Number(data[0].id);
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: error.text()})),
          () => this.router.navigate(['/subsecretarias', Number(this._component_entidad_id), 'ver'])
        );
    }
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.getListas();
  }
}
