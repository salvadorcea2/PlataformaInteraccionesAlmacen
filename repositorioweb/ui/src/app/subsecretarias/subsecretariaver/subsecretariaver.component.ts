import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Ministerio } from '../../models/ministerio.model';
import { Subsecretaria } from '../../models/subsecretaria.model';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';
import { ApiService } from '../../services/api.service';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';

@Component({
  selector: 'app-subsecretariaver',
  templateUrl: './subsecretariaver.component.html',
  styleUrls: ['./subsecretariaver.component.css']
})
export class SubsecretariaverComponent implements OnInit {

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
    if ( typeof this._component_entidad_id !== 'undefined' && this._component_entidad_id !== null && this._component_entidad_id !== 0 ) {
      this._apiService.load(this._component_entidad, this._component_entidad_id)
        .subscribe(
          (data: any) => {
            this.item = new Subsecretaria(data.pop());
          },
          (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al cargar la subsecretaría ID: ' + this._component_entidad_id}))
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
        (data: any) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó la subsecretaría "' + this.item.nombre + '" con éxito'})),
        (error) => this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'danger', dismissible: true, istimeout: true, timeout:10000, msg: 'Error al eliminar la subsecretaría "' + this.item.nombre + '"'})),
        () => {
          this.router.navigate(['/ministerios']);
          this._alertasService.onNuevaAlerta.emit(new Alerta({type: 'success', dismissible: true, istimeout: true, timeout:10000, msg: 'Se eliminó la subsecretaría "' + this.item.nombre + '" con éxito'}));
        }
      );
  }

  ngOnInit() {
    this._component_entidad_id = this.route.snapshot.params['id'];
    this.getListas();
  }
}
