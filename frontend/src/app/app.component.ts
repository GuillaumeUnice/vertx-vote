import { Component } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private http: Http) { }

  public choice(choice: String): void {
    this.http.post('http://localhost:8080/',
      JSON.stringify({vote: choice}),
      {headers: new Headers({'Content-Type': 'application/json'})}
    ).subscribe(
      data => {
        console.log(data);
        
      },
      err => { console.log(err) }
    );
  }
}
