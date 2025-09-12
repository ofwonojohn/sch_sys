from pydantic import BaseModel
from datetime import date
from typing import Optional
from app.models import LevelEnum

class StudentBase(BaseModel):
    first_name: str
    last_name: str
    date_of_birth: date
    level: LevelEnum
    student_number: str

class StudentCreate(StudentBase):
    pass

class Student(StudentBase):
    id: int

    class Config:
        orm_mode = True
