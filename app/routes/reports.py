from fastapi import APIRouter, Depends, Request
from sqlalchemy.orm import Session
from typing import List
from app import models, schemas
from app.database import SessionLocal
from app.curriculum import O_LEVEL_SUBJECTS, A_LEVEL_SUBJECTS
from fastapi.templating import Jinja2Templates

router = APIRouter()
templates = Jinja2Templates(directory="app/templates")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/students-by-level/{level}")
def get_students_by_level(level: str, db: Session = Depends(get_db)):
    students = db.query(models.Student).filter(models.Student.level == level).all()
    return {"level": level, "students": [schemas.Student.from_orm(s) for s in students]}

@router.get("/curriculum/{level}")
def get_curriculum(level: str):
    if level == "O'level":
        return {"level": level, "subjects": O_LEVEL_SUBJECTS}
    elif level == "A'level":
        return {"level": level, "subjects": A_LEVEL_SUBJECTS}
    else:
        return {"error": "Invalid level"}

@router.get("/curriculum/{level}/html")
def get_curriculum_html(level: str, request: Request):
    if level == "O'level":
        subjects = O_LEVEL_SUBJECTS
    elif level == "A'level":
        subjects = A_LEVEL_SUBJECTS
    else:
        subjects = []
    return templates.TemplateResponse("curriculum.html", {"request": request, "level": level, "subjects": subjects})
