import streamlit as st
import requests

st.set_page_config(page_title="EduFlow Analytics", layout="wide")
st.title("📊 EduFlow Analytics Dashboard")

# Example: Connect to your Spring Boot API
API_URL = "http://localhost:8080/api"

# Your Streamlit components here
st.write("Welcome to EduFlow Analytics!")