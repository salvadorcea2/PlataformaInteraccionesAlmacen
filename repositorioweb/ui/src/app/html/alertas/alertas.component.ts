import { Component, OnInit } from '@angular/core';
import { AlertasService } from '../../services/alertas.service';
import { Alerta } from '../../models/alerta.model';

@Component({
  selector: 'app-alertas',
  templateUrl: './alertas.component.html',
  styleUrls: ['./alertas.component.css']
})
export class AlertasComponent implements OnInit {

  public alerts: Alerta[] = [];
  constructor(private _alertasService: AlertasService) { }

  ngOnInit() {
    this._alertasService.onNuevaAlerta
      .subscribe(
        (alerta: Alerta) => this.alerts.push(alerta)
      );
  }

}
