import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Plantilla } from '../../models/plantilla.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';

@Component({
  selector: 'app-plantillaver',
  templateUrl: './plantillaver.component.html',
  styleUrls: ['./plantillaver.component.css']
})
export class PlantillaverComponent implements OnInit {

  public _component_entidad: string = 'plantilla';
  public _component_entidad_id: number;

  public item: Plantilla = new Plantilla({});
  public modalRef: BsModalRef;

  constructor(private _apiService: ApiService, private _alertasService: AlertasService, private router: Router, private route: ActivatedRoute, private modalService: BsModalService) { }

  updateItem() {
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Plantilla(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar la plantilla ID: ' + this._component_entidad_id}))
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
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó la plantilla "' + this.item.nombre + '" con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar la plantilla "' + this.item.nombre + '"'})),
        () => {
          this.router.navigate(['/plantillas']);
          this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó la plantilla "' + this.item.nombre + '" con éxito'}));
        }
      );
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.updateItem();
  }
}
