from fastapi import FastAPI, Request
from fastapi.templating import Jinja2Templates
from app.routes import students, reports
from app.database import engine
from app import models

models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="Secondary School System")
templates = Jinja2Templates(directory="app/templates")

app.include_router(students.router, prefix="/students", tags=["students"])
app.include_router(reports.router, prefix="/reports", tags=["reports"])

@app.get("/")
def read_root(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})
